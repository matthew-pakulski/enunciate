/*
 * Copyright 2006-2008 Web Cohesion
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webcohesion.enunciate.modules.jackson1.model;

import com.webcohesion.enunciate.javac.decorations.Annotations;
import com.webcohesion.enunciate.javac.decorations.type.DecoratedTypeMirror;
import com.webcohesion.enunciate.modules.jackson1.EnunciateJackson1Context;
import com.webcohesion.enunciate.modules.jackson1.model.types.JsonClassType;
import com.webcohesion.enunciate.modules.jackson1.model.types.JsonType;
import com.webcohesion.enunciate.modules.jackson1.model.types.JsonTypeFactory;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.xml.bind.annotation.*;
import java.beans.Introspector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;

/**
 * An accessor that is marshalled in json to an json element.
 *
 * @author Ryan Heaton
 */
@SuppressWarnings ( "unchecked" )
public class Member extends Accessor {

  private final JsonProperty propertyInfo;
  private final Collection<Member> choices;
  private final DecoratedTypeMirror explicitType;
  private final String explicitName;
  private final JsonTypeInfo.As subtypeIdInclusion;

  public Member(javax.lang.model.element.Element delegate, TypeDefinition typedef, EnunciateJackson1Context context) {
    super(delegate, typedef, context);

    this.propertyInfo = getAnnotation(JsonProperty.class);
    this.choices = new ArrayList<Member>();
    JsonSubTypes subTypes = getAnnotation(JsonSubTypes.class);
    JsonTypeInfo.As typeIdInclusion = null;
    JsonTypeInfo typeInfo = getAnnotation(JsonTypeInfo.class);
    if (typeInfo != null) {
      typeIdInclusion = typeInfo.include();
    }

    XmlElements xmlElements = getAnnotation(XmlElements.class);
    XmlElementRefs xmlElementRefs = getAnnotation(XmlElementRefs.class);

    if (subTypes != null && subTypes.value().length > 0) {
      for (final JsonSubTypes.Type element : subTypes.value()) {
        DecoratedTypeMirror choiceType = Annotations.mirrorOf(new Callable<Class<?>>() {
          @Override
          public Class<?> call() throws Exception {
            return element.value();
          }
        }, this.env);
        if (choiceType != null) {
          String choiceTypeId = element.name();
          if ("".equals(choiceTypeId)) {
            //try to look at the type.
            if (choiceType.isDeclared()) {
              TypeElement choiceElement = (TypeElement) ((DeclaredType) choiceType).asElement();
              if (choiceElement != null) {
                JsonTypeName typeName = choiceElement.getAnnotation(JsonTypeName.class);
                if (typeName != null) {
                  choiceTypeId = typeName.value();
                }

                JsonTypeInfo choiceTypeInfo = choiceElement.getAnnotation(JsonTypeInfo.class);
                if ((choiceTypeId == null || "".equals(choiceTypeId)) && choiceTypeInfo != null && choiceTypeInfo.use() == JsonTypeInfo.Id.CLASS) {
                  choiceTypeId = choiceElement.getQualifiedName().toString();
                }

                if (typeIdInclusion == null && choiceTypeInfo != null) {
                  typeIdInclusion = choiceTypeInfo.include();
                }
              }
            }
          }

          if (choiceTypeId == null || "".equals(choiceTypeId)) {
            //can't fail because there could be other ids. I guess we'll just say "".
            choiceTypeId = "";
          }

          this.choices.add(new Member(getDelegate(), getTypeDefinition(), choiceType, choiceTypeId, context));
        }
      }
    }
    else if (context.isHonorJaxb() && (xmlElements != null || xmlElementRefs != null)) {
      typeIdInclusion = JsonTypeInfo.As.WRAPPER_OBJECT;

      if (xmlElements != null) {
        for (final XmlElement xmlElement : xmlElements.value()) {
          DecoratedTypeMirror choiceType = Annotations.mirrorOf(new Callable<Class<?>>() {
            @Override
            public Class<?> call() throws Exception {
              return xmlElement.type();
            }
          }, this.env);
          if (choiceType != null) {
            String choiceTypeId = xmlElement.name();

            if ("##default".equals(choiceTypeId)) {
              choiceTypeId = "";
            }

            this.choices.add(new Member(getDelegate(), getTypeDefinition(), choiceType, choiceTypeId, context));
          }
        }
      }

      if (xmlElementRefs != null) {
        for (final XmlElementRef elementRef : xmlElementRefs.value()) {
          DecoratedTypeMirror choiceType = Annotations.mirrorOf(new Callable<Class<?>>() {
            @Override
            public Class<?> call() throws Exception {
              return elementRef.type();
            }
          }, this.env);
          if (choiceType != null) {
            String choiceTypeId = elementRef.name();

            if ("##default".equals(choiceTypeId)) {
              TypeElement choiceElement = (TypeElement) ((DeclaredType) choiceType).asElement();
              if (choiceElement != null) {
                XmlRootElement rootElement = choiceElement.getAnnotation(XmlRootElement.class);
                if (rootElement != null) {
                  choiceTypeId = rootElement.name();
                }

                if ("##default".equals(choiceTypeId)) {
                  choiceTypeId = Introspector.decapitalize(choiceElement.getSimpleName().toString());
                }
              }
              else {
                choiceTypeId = "";
              }
            }

            this.choices.add(new Member(getDelegate(), getTypeDefinition(), choiceType, choiceTypeId, context));
          }
        }
      }
    }
    else {
      this.choices.add(this);
    }

    this.explicitType = null;
    this.explicitName = null;
    this.subtypeIdInclusion = typeIdInclusion;
  }

  protected Member(javax.lang.model.element.Element delegate, TypeDefinition typedef, DecoratedTypeMirror explicitType, String explicitName, EnunciateJackson1Context context) {
    super(delegate, typedef, context);
    this.propertyInfo = null;
    this.choices = new ArrayList<Member>();
    this.choices.add(this);
    this.explicitName = explicitName;
    this.explicitType = explicitType;
    this.subtypeIdInclusion = null;
  }

  // Inherited.
  public String getName() {
    if (this.explicitName != null) {
      return this.explicitName;
    }

    String propertyName = getSimpleName().toString();

    if (this.context.isHonorJaxb()) {
      if (getAnnotation(XmlValue.class) != null) {
        propertyName = "value";
      }

      if (getAnnotation(XmlElementRef.class) != null) {
        DecoratedTypeMirror accessorType = getAccessorType();
        if (accessorType.isDeclared()) {
          TypeElement typeElement = (TypeElement) ((DeclaredType) accessorType).asElement();
          XmlRootElement elementInfo = typeElement.getAnnotation(XmlRootElement.class);
          if (elementInfo != null) {
            propertyName = elementInfo.name();
          }

          if ("##default".equals(propertyName)) {
            propertyName = Introspector.decapitalize(typeElement.getSimpleName().toString());
          }
        }
      }

      XmlAttribute attributeInfo = getAnnotation(XmlAttribute.class);
      if (attributeInfo != null && !"##default".equals(attributeInfo.name())) {
        propertyName = attributeInfo.name();
      }

      XmlElement elementInfo = getAnnotation(XmlElement.class);
      if (elementInfo != null && !"##default".equals(elementInfo.name())) {
        propertyName = elementInfo.name();
      }
    }

    if ((propertyInfo != null) && (!"".equals(propertyInfo.value()))) {
      propertyName = propertyInfo.value();
    }

    return propertyName;
  }

  /**
   * The type of an element accessor can be specified by an annotation.
   *
   * @return The accessor type.
   */
  @Override
  public DecoratedTypeMirror getAccessorType() {
    if (this.explicitType != null) {
      return this.explicitType;
    }

    JsonType specifiedJsonType = JsonTypeFactory.findSpecifiedType(this, this.context);
    DecoratedTypeMirror specifiedType = specifiedJsonType instanceof JsonClassType ? (DecoratedTypeMirror) ((JsonClassType) specifiedJsonType).getTypeDefinition().asType() : null;

    if (specifiedType != null) {
      return specifiedType;
    }

    return super.getAccessorType();
  }

  /**
   * Whether this element is required.
   *
   * @return Whether this element is required.
   */
  public boolean isRequired() {
    return false;
  }

  /**
   * The default value, or null if none exists.
   *
   * @return The default value, or null if none exists.
   */
  public String getDefaultValue() {
    return null;
  }

  /**
   * The choices for this element.
   *
   * @return The choices for this element.
   */
  public Collection<? extends Member> getChoices() {
    return choices;
  }

  /**
   * How subtypes id are included in the payload.
   *
   * @return How subtypes id are included in the payload.
   */
  public JsonTypeInfo.As getSubtypeIdInclusion() {
    return subtypeIdInclusion;
  }

}
